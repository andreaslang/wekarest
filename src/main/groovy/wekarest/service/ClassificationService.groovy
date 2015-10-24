package wekarest.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import weka.classifiers.Classifier
import weka.classifiers.Evaluation
import weka.core.Instances
import weka.core.Utils
import wekarest.model.ClassificationOptions
import wekarest.model.ClassificationResult

import static weka.core.Utils.splitOptions

@Service
class ClassificationService {

    @Autowired
    ConfigurationService configurationService

    ClassificationResult classify(ClassificationOptions options, Instances dataSet) {
        def (Instances trainingSet, Instances testSet) = splitIntoTrainingAndTestSet(dataSet)
        return classify(options, trainingSet, testSet)
    }

    ClassificationResult classify(Classifier classifier, Instances dataSet) {
        def (Instances trainingSet, Instances testSet) = splitIntoTrainingAndTestSet(dataSet)
        return classify(classifier, trainingSet, testSet)
    }

    ClassificationResult classify(String classifierName, Instances trainingSet, Instances testSet) {
        return classify(new ClassificationOptions(classifier: classifierName), trainingSet, testSet)
    }

    ClassificationResult classify(ClassificationOptions options, Instances trainingSet, Instances testSet) {
        def classifierConfig = configurationService.getClassifierConfig(options.classifier)
        def className = classifierConfig.classifier
        Classifier classifier = loadClassifier(className)
        classifier.setOptions(getWekaClassifierOptions(options, classifierConfig))
        return classify(classifier, trainingSet, testSet)
    }

    private Classifier loadClassifier(className) {
        def clazz = getClass().getClassLoader().loadClass(className)
        def classifier = clazz.newInstance() as Classifier
        return classifier
    }

    private String[] getWekaClassifierOptions(ClassificationOptions classificationOptions, Map map) {
        def wekaOptions
        def arguments = classificationOptions.classifierArguments
        if (arguments)
            wekaOptions = splitOptions(arguments)
        else
            wekaOptions = splitOptions(map.defaultClassifierArguments)
        return wekaOptions
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
