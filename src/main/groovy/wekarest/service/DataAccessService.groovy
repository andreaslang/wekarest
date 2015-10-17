package wekarest.service

import wekarest.model.DataFile
import wekarest.mongodb.FileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import weka.core.Instances
import weka.core.converters.CSVLoader

@Service
class DataAccessService {

    @Autowired
    FileRepository fileRepository;

    Instances loadDataSet(String hash) {
        def dataFile = fileRepository.findOne(hash)
        CSVLoader loader = new CSVLoader()
        loader.setSource(new ByteArrayInputStream(dataFile.data.unzip()))
        def dataSet = loader.getDataSet()
        if (dataSet.classIndex() == -1)
            dataSet.setClassIndex(dataSet.numAttributes() - 1);
        return dataSet
    }

    DataFile storeFile(byte[] content) {
        def zippedBytes = content.zip()
        def hash = content.asMD5()
        def dataFile = new DataFile(hash: hash, data: zippedBytes)
        fileRepository.save(dataFile)
        return dataFile
    }
}
