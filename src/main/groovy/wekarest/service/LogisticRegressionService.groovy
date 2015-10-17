package wekarest.service

import wekarest.model.ClassificationResult
import org.springframework.stereotype.Service
import weka.classifiers.functions.Logistic
import weka.core.Instances

import static weka.core.Utils.splitOptions

@Service
class LogisticRegressionService extends ClassificationService {

    ClassificationResult classify(Instances dataSet) {
        def (Instances trainingSet, Instances testSet) = splitIntoTrainingAndTestSet(dataSet)
        def logistic = new Logistic()
        logistic.options = splitOptions('-R 1.0E-8 -M -1')
        return classify(logistic, trainingSet, testSet)
    }

}
