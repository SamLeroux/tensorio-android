package ai.doc.tensorio.pytorch.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.pytorch.Tensor;

import java.nio.ByteBuffer;

import ai.doc.tensorio.core.layerinterface.LayerDescription;

/* Pytorch models use Tensors to write data into models and read data out of them. Tensors can be seen as wrappers around ByteBuffers

 */
public interface Converter {

    /**
     * Creates a ByteBuffer to hold data for input or output to a Pytorch model using the parameters
     * in the layer description.
     *
     * @param description A description of the layer to create a byte buffer for
     * @return ByteBuffer ready to be filled with input or output data.
     */
    public ByteBuffer createBackingBuffer(@NonNull LayerDescription description);

    /**
     * Converts an Object to a Tensor, used to prepare data for a writing into a model.
     *
     * @param o           One of a number of types that can be converted into a Tensor
     * @param description A description of the layer with instructions on how to make the conversion
     * @param cache       A pre-existing byte buffer to use, which will be used if not null. If a cache
     *                    is provided it will be rewound before being used.
     * @return Tensor ready for use with a Pytorch model
     * @throws IllegalArgumentException Raised if the input object o is not of one of the supported
     *                                  types or is the wrong length
     */
    public Tensor toTensor(@NonNull Object o, @NonNull LayerDescription description, @Nullable ByteBuffer cache) throws IllegalArgumentException;

    /**
     * Converts a Tensor to an object, used to read data from a model.
     *
     * @param t      A Tensor
     * @param description A description of the layer with instructions on how to make the conversion
     * @return One of a number of native types such as an array of floats or a Bitmap
     */
    public Object fromTensor(@NonNull Tensor t, @NonNull LayerDescription description);
}
