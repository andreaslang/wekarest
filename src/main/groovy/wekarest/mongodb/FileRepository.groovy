package wekarest.mongodb

import org.springframework.data.mongodb.repository.MongoRepository
import wekarest.model.Data

interface FileRepository extends MongoRepository<Data, String> {

    Data findByHash(String hash)

}
