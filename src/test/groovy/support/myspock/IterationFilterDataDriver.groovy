package support.myspock

import org.spockframework.runtime.IDataIterator
import org.spockframework.runtime.extension.IDataDriver
import org.spockframework.runtime.extension.IIterationRunner
import org.spockframework.runtime.model.ParameterInfo

class IterationFilterDataDriver implements IDataDriver {

    private final List<Integer> iterationIndices

    IterationFilterDataDriver(int[] iterationIndicesToRun) {
        this.iterationIndices = iterationIndicesToRun //from CustomSpec: onlyRunIterationIndices
    }

    @Override
    void runIterations(IDataIterator dataIterator, IIterationRunner iterationRunner, List<ParameterInfo> parameters) {
        int estimatedNumIterations = dataIterator.getEstimatedNumIterations()
        int indxCounter = 0
        def indicesToRun = this.iterationIndices ? this.iterationIndices.findAll { it != null } : []
        while (dataIterator.hasNext()) {
            Object[] arguments = dataIterator.next() //flow continues
            //run iteration unless specified otherwise in CustomSpec: onlyRunIterationIndices
            if(arguments != null && (!indicesToRun || (indicesToRun && indicesToRun.contains(indxCounter)))) {
                iterationRunner.runIteration(IDataDriver.prepareArgumentArray(arguments, parameters), estimatedNumIterations)
            }
            indxCounter++
        }
    }
}