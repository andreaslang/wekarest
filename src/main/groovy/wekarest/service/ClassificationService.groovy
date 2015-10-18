package wekarest.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import weka.classifiers.Classifier
import weka.classifiers.Evaluation
import weka.core.Instances
import wekarest.model.ClassificationResult

@Service
class ClassificationService {

    @Autowired
    ConfigurationService configurationService

    ClassificationResult classify(String classifierName, Instances dataSet) {
        def (Instances trainingSet, Instances testSet) = splitIntoTrainingAndTestSet(dataSet)
        classify(classifierName, trainingSet, testSet)
    }

    ClassificationResult classify(Classifier classifier, Instances dataSet) {
        def (Instances trainingSet, Instances testSet) = splitIntoTrainingAndTestSet(dataSet)
        classify(classifier, trainingSet, testSet)
    }

    ClassificationResult classify(String classifierName, Instances trainingSet, Instances testSet) {
        def classifierConfig = configurationService.getClassifierConfig(classifierName)
        def className = classifierConfig.classifier
        Classifier classifier = loadClassifier(className)
        classify(classifier, trainingSet, testSet)
    }

    private Classifier loadClassifier(className) {
        def clazz = getClass().getClassLoader().loadClass(className)
        def classifier = clazz.newInstance() as Classifier
        classifier
    }

    ClassificationResult classify(Classifier classifier, Instances trainingSet, Instances testSet) {
        classifier.buildClassifier(trainingSet)
        def evaluation = new Evaluation(trainingSet);
        evaluation.evaluateModel(classifier, testSet);
        return new ClassificationResult(
                summary: evaluation.toSummaryString("\nResults\n======\n", false),
                details: evaluation.toClassDetailsString()
        )
    }

    static List splitIntoTrainingAndTestSet(Instances dataSet, double splitRatio = 0.75) {
        def totalSize = dataSet.numInstances()
        int trainingSize = totalSize * splitRatio
        int testSize = totalSize - trainingSize
        def trainingSet = new Instances(dataSet, 0, trainingSize)
        def testSet = new Instances(dataSet, trainingSize, testSize)
        return [trainingSet, testSet]
    }

}
