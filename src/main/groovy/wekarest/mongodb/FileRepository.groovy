package wekarest.mongodb

import org.springframework.data.mongodb.repository.MongoRepository
import wekarest.model.DataFile

interface FileRepository extends MongoRepository<DataFile, String> {

    DataFile findByHash(String hash)

}
