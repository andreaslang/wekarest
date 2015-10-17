package wekarest.model

import org.springframework.data.annotation.Id

class DataFile {

    @Id
    String hash
    byte[] data

}
