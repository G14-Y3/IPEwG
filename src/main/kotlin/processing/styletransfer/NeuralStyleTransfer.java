package processing.styletransfer;

import javafx.scene.image.WritableImage;
import org.jetbrains.annotations.NotNull;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import processing.ImageProcessing;

import java.util.Arrays;
import java.util.Map;

enum NeuralStyles {

}

public class NeuralStyleTransfer implements ImageProcessing {
    Module mod;

    public static Map<NeuralStyles, String> styleToPath = Map.of();

    public NeuralStyleTransfer(String modelpath) {
        mod = Module.load(modelpath);
    }

    public NeuralStyleTransfer(NeuralStyles styles) {
        mod = Module.load(styleToPath.get(styles));
    }

    @Override
    public void process(@NotNull WritableImage image) {
        Tensor data =
                Tensor.fromBlob(
                        new int[] {1, 2, 3, 4, 5, 6}, // data
                        new long[] {2, 3} // shape
                );
        IValue result = mod.forward(IValue.from(data), IValue.from(3.0));
        Tensor output = result.toTensor();
        System.out.println("shape: " + Arrays.toString(output.shape()));
        System.out.println("data: " + Arrays.toString(output.getDataAsFloatArray()));
    }
}
