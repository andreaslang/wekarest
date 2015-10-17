package wekarest.service

import wekarest.model.ClassificationResult
import org.springframework.stereotype.Service
import weka.classifiers.functions.SMO
import weka.core.Instances

import static weka.core.Utils.splitOptions

@Service
class SMOService extends ClassificationService {

    ClassificationResult classify(Instances dataSet) {
        def (Instances trainingSet, Instances testSet) = splitIntoTrainingAndTestSet(dataSet)
        def smo = new SMO()
        smo.options = splitOptions('-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0"')
        return classify(smo, trainingSet, testSet)
    }
}
