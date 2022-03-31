package se.sundsvall.emailsender.api.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SenderTests {

    @Test
    void testGettersAndSetters() {
        var sender = new Sender();
        sender.setName("someName");
        sender.setAddress("someAddress");
        sender.setReplyTo("someReplyTo");

        assertThat(sender.getName()).isEqualTo("someName");
        assertThat(sender.getAddress()).isEqualTo("someAddress");
        assertThat(sender.getReplyTo()).isEqualTo("someReplyTo");
    }

    @Test
    void testBuilder() {
        var sender = Sender.builder()
            .withName("someName")
            .withAddress("someAddress")
            .withReplyTo("someReplyTo")
            .build();

        assertThat(sender.getName()).isEqualTo("someName");
        assertThat(sender.getAddress()).isEqualTo("someAddress");
        assertThat(sender.getReplyTo()).isEqualTo("someReplyTo");
    }
}
