package wekarest.model

import org.springframework.data.annotation.Id

class Data {

    @Id
    String hash
    byte[] zippedContent

    String asString() {
        return new String(zippedContent)
    }

}
