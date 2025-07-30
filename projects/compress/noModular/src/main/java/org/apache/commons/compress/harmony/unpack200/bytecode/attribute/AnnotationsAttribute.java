/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.harmony.unpack200.bytecode.attribute;

import org.apache.commons.compress.harmony.unpack200.bytecode.CP.CPConstant;
import org.apache.commons.compress.harmony.unpack200.bytecode.CP.CPNameAndType;
import org.apache.commons.compress.harmony.unpack200.bytecode.CP.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassConstantPool;
import org.apache.commons.compress.harmony.unpack200.bytecode.ClassFileEntry;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstracts Annotations attributes.
 */
public abstract class AnnotationsAttribute extends Attribute {

    /**
     * Constructs a new instance for an attribute name.
     *
     * @param attributeName an attribute name.
     */
    public AnnotationsAttribute(final CPUTF8 attributeName) {
        super(attributeName);
    }

}
