package wekarest.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import weka.core.Instances
import weka.core.converters.CSVLoader
import wekarest.model.Data
import wekarest.mongodb.FileRepository

@Service
class DataAccessService {

    @Autowired
    FileRepository fileRepository;

    Instances loadDataSet(String hash) {
        def data = load(hash)
        CSVLoader loader = new CSVLoader()
        loader.setSource(new ByteArrayInputStream(data.content.unzip()))
        def dataSet = loader.getDataSet()
        if (dataSet.classIndex() == -1)
            dataSet.setClassIndex(dataSet.numAttributes() - 1);
        return dataSet
    }

    Data load(String hash) {
        return fileRepository.findOne(hash)
    }

    Data store(String content) {
        store(content.bytes)
    }

    Data store(byte[] content) {
        def zippedBytes = content.zip()
        def hash = content.asMD5()
        def dataFile = new Data(hash: hash, content: zippedBytes)
        fileRepository.save(dataFile)
        return dataFile
    }
}
