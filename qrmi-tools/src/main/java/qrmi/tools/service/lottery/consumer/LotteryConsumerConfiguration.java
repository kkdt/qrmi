/** 
 * Copyright (C) 2019 thinh ho
 * This file is part of 'qrmi' which is released under the MIT license.
 * See LICENSE at the project root directory.
 */
package qrmi.tools.service.lottery.consumer;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import qrmi.core.RabbitExportConfiguration;

@Configuration
@Import(value = {RabbitExportConfiguration.class})
public class LotteryConsumerConfiguration {

}
