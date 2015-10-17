package wekarest.mongodb

import wekarest.model.DataFile
import org.springframework.data.mongodb.repository.MongoRepository

interface FileRepository extends MongoRepository<DataFile, String> {

    DataFile findByHash(String hash)

}
