package wekarest.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import weka.core.Instances
import wekarest.model.Data
import wekarest.model.FileMetaData
import wekarest.mongodb.FileRepository

@Service
class DataAccessService {

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    FileRepository fileRepository;

    Instances loadDataSet(FileMetaData metaData) {
        def hash = metaData.dataHash
        def data = load(hash)
        def loader = getLoader(metaData)
        loader.setSource(new ByteArrayInputStream(data.zippedContent.unzip()))
        def dataSet = loader.getDataSet()
        if (dataSet.classIndex() == -1)
            dataSet.setClassIndex(dataSet.numAttributes() - 1);
        return dataSet
    }

    private Object getLoader(FileMetaData metaData) {
        def instanceLoaderClassName = configurationService.getInstanceLoaderClassName(metaData.type)
        def clazz = getClass().getClassLoader().loadClass(instanceLoaderClassName)
        def loader = clazz.newInstance()
        return loader
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
        def dataFile = new Data(hash: hash, zippedContent: zippedBytes)
        fileRepository.save(dataFile)
        return dataFile
    }
}
