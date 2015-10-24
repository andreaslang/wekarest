classifiers {
    svm(
        classifier: 'weka.classifiers.functions.SMO',
        defaultClassifierArguments: '-C 1.0 -L 0.0010 -P 1.0E-12 -N 0 -V -1 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0"'
    )
    logistic(
        classifier: 'weka.classifiers.functions.Logistic',
        defaultClassifierArguments: '-R 1.0E-8 -M -1'
    )
    'naive-bayes'(
        classifier: 'weka.classifiers.bayes.NaiveBayes',
        defaultClassifierArguments: ''
    )
}
instanceLoaders {
    'text/csv'('weka.core.converters.CSVLoader')
}
