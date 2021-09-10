package app.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Autowired

@Configuration
class MessagingConfiguration {

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Bean
    fun requestQueue() = Queue("request-send")

    @Bean
    fun requestStateChangeQueue() = Queue("request-state-change")

    @Bean
    fun objectMapper(): MessageConverter = Jackson2JsonMessageConverter(mapper)


}