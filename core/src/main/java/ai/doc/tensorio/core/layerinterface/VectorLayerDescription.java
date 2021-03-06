/*
 * VectorLayerDescription.java
 * TensorIO
 *
 * Created by Philip Dow on 7/6/2020
 * Copyright (c) 2020 - Present doc.ai (http://doc.ai)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.doc.tensorio.core.layerinterface;

import java.util.HashMap;
import java.util.Map;

import ai.doc.tensorio.core.data.Dequantizer;
import ai.doc.tensorio.core.data.Quantizer;

import static java.lang.Math.abs;

/**
 * The description of a vector (array) input or output later.
 *
 * Vector inputs and outputs are always unrolled vectors, and from the tensor's perspective they are
 * just an array of bytes. The total length of a vector will be the total volume of the layer.
 * For example, if an input layer is a tensor of shape `(24,24,2)`, the length of the vector will be
 * `24x24x2 = 1152`.
 *
 * TensorFlow and TensorFlow Lite models expect row major ordering of bytes,
 * such that higher order dimensions are traversed first. For example, a 2x4 matrix
 * with the following values:
 *
 * @code
 * [[1 2 3 4]
 *  [5 6 7 8]]
 * @endcode
 *
 * should be unrolled and provided to the model as:
 *
 * @code
 * [1 2 3 4 5 6 7 8]
 * @endcode
 *
 * i.e, start with the row and traverse the columns before moving to the next row.
 *
 * Because output layers are also exposed as an array of bytes, a `TFLiteModel` will always return
 * a vector in one dimension. If is up to you to reshape it if required.
 *
 * @warning
 * A `VectorLayerDescription`'s length is different than the byte length of a `Data` object.
 * For example a quantized `Vector` (uint8_t) of length 4 will occupy 4 bytes of memory but an
 * unquantized `Vector` (float_t) of length 4 will occupy 16 bytes of memory.
 */

public class VectorLayerDescription extends LayerDescription {

    /**
     * The shape of the underlying layer
     */

    private final int[] shape;

    /**
     * The length of the vector in terms of its number of elements.
     */

    private int length;

    /**
     * true if there are labels associated with this layer, false otherwise.
     */

    private boolean labeled;

    /**
     * Indexed labels corresponding to the indexed output of a layer. May be null.
     *
     * Labeling the output of a model is such a common operation that support for it is included
     * by default.
     */

    private String[] labels;

    /**
     * A function that converts a vector from unquantized values to quantized values
     */

    private Quantizer quantizer;

    /**
     * A function that converts a vector from quantized values to unquantized values
     */

    private Dequantizer dequantizer;

    /**
     * Designated initializer. Creates a vector description from the properties parsed in a model.json
     * file.
     *
     * @param shape       The layer's shape
     * @param batched     True if the layer supports batched execution, false otherwise
     * @param labels      The indexed labels associated with the outputs of this layer. May be `nil`.
     * @param quantized   True if the values are quantized
     * @param quantizer   A function that transforms unquantized values to quantized input
     * @param dequantizer A function that transforms quantized output to unquantized values
     */

    public VectorLayerDescription(int[] shape, boolean batched, String[] labels, boolean quantized, Quantizer quantizer, Dequantizer dequantizer, DataType dtype) {
        this.shape = shape;

        // Total Volume
        this.length = 1;
        for (int i : shape) {
            length *= abs(i);
        }

        this.batched = batched;
        this.labels = labels;
        this.labeled = labels != null && labels.length > 0;
        this.quantized = quantized;
        this.quantizer = quantizer;
        this.dequantizer = dequantizer;
        this.dtype = dtype;
    }

    //region Getters and Setters

    public int[] getShape() {
        return shape;
    }

    public int getLength() {
        return length;
    }

    public String[] getLabels() {
        return labels;
    }

    public boolean isLabeled() {
        return labeled;
    }

    public Quantizer getQuantizer() {
        return quantizer;
    }

    public Dequantizer getDequantizer() {
        return dequantizer;
    }

    @Override
    public int[] getTensorShape() {
        return getShape();
    }

    //endRegion

    /**
     * Given the output vector of a tensor, returns labeled outputs using `labels`.
     *
     * @param vector An array of float values.
     * @return  The labeled values, where the dictionary keys are the labels and the
     * dictionary values are the associated vector values.
     */

    public Map<String, Float> labeledValues(float[] vector) {
        if (!isLabeled()) {
            return null;
        }

        Map<String, Float> result = new HashMap<>(vector.length);

        for (int i = 0; i < labels.length; i++){
            result.put(labels[i], vector[i]);
        }

        return result;
    }

}
