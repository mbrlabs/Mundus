/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.ui.widgets;

import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;

/**
 * @author Marcus Brummer
 * @version 18-02-2016
 */
public class FloatFieldWithLabel extends TextFieldWithLabel {

    public FloatFieldWithLabel(String labelText, int width, boolean allowNegative) {
        super(labelText, width);
        textField.setTextFieldFilter(new FloatDigitsOnlyFilter(allowNegative));
    }

    public FloatFieldWithLabel(String labelText, int width) {
        super(labelText, width);
        textField.setTextFieldFilter(new FloatDigitsOnlyFilter(true));
    }

    public float getFloat() {
        if(textField.getText().isEmpty()) {
            return 0;
        }
        return Float.parseFloat(textField.getText());
    }

}
