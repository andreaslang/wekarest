package wekarest.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import weka.core.Attribute
import weka.core.Instances
import wekarest.model.ClassificationOptions
import wekarest.model.Data
import wekarest.model.FileMetaData
import wekarest.mongodb.FileRepository

@Service
class DataAccessService {

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    FileRepository fileRepository;

    Instances loadDataSet(FileMetaData metaData, ClassificationOptions classificationOptions) {
        def classProperty = classificationOptions.classProperty
        def excludedProperties = classificationOptions.excludedProperties
        def hash = metaData.dataHash
        def data = load(hash)
        def loader = getLoader(metaData)
        loader.setSource(new ByteArrayInputStream(data.zippedContent.unzip()))
        def dataSet = loader.getDataSet() as Instances
        setClassPropertyAndRemoveExcludedProperties(dataSet, classProperty, excludedProperties)
        return dataSet
    }

    private void setClassPropertyAndRemoveExcludedProperties(Instances dataSet, String classProperty, Set<String> excludedProperties) {
        if (dataSet.classIndex() == -1)
            dataSet.setClassIndex(dataSet.numAttributes() - 1);
        def attributes = dataSet.enumerateAttributes()
        def excludedAttributes = []
        for (Attribute attribute in attributes) {
            def attributeName = attribute.name()
            if (attributeName == classProperty)
                dataSet.setClass(attribute)
            else if (excludedProperties && excludedProperties.contains(attributeName))
                excludedAttributes << attribute
        }
        excludedAttributes.each { dataSet.delete(it.index()) }
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
