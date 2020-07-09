package qrmi.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.remoting.support.RemoteInvocation;
import qrmi.core.annotation.JsonModel;

public class RabbitRemoteMessageConverter implements MessageConverter, Function<MethodInvocation, RemoteInvocation> {
    private final ObjectMapper mapper = new ObjectMapper();

    private Function<RemoteInvocation, Object[]> jsonArguments = invocation -> {
        Object[] ret = new Object[invocation.getArguments().length];
        Object[] arguments = invocation.getArguments();
        for(int i = 0; i < arguments.length; i++) {
            Object arg = arguments[i];
            Object json = Optional.ofNullable(AnnotationUtils.findAnnotation(arg.getClass(), JsonModel.class))
                // json serialize attempt
                .map(o -> (Object)mapper.convertValue(o, Map.class))
                // default back to the original object
                .orElse(arg);
            ret[i] = json;
        }
        return ret;
    };

    @Override
    public RemoteInvocation apply(MethodInvocation method) {
        RemoteInvocation invocation = new RemoteInvocation(method);
        invocation.setArguments(jsonArguments.apply(invocation));
        return invocation;
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        if(object instanceof RemoteInvocation) {
            RemoteInvocation invocation = (RemoteInvocation)object;
            invocation.setArguments(jsonArguments.apply(invocation));
            Jackson2JsonMessageConverter jackson = new Jackson2JsonMessageConverter();
            Message message = jackson.toMessage(invocation, messageProperties);
            return message;
        }
        throw new MessageConversionException(String.format("Unknown object to convert to AMQP message: %s",
            object != null ? object.getClass().getName() : "NULL"));
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return null;
    }
}
