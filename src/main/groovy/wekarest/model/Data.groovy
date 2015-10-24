package wekarest.model

import org.springframework.data.annotation.Id

class Data {

    @Id
    String hash
    byte[] content

    String asString() {
        return new String(content)
    }

}
