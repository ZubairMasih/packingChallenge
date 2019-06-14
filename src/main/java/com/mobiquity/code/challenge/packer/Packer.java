/**
 * Copyright 2019 Esfandiyar Talebi
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

package com.mobiquity.code.challenge.packer;

import com.mobiquity.code.challenge.exception.ApiException;
import com.mobiquity.code.challenge.extractor.DataExtractor;
import com.mobiquity.code.challenge.models.Item;
import com.mobiquity.code.challenge.models.Package;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

/*
       The Packer class implements the main logic for data evaluation and processing. It uses Java 8 streams and RegExp

       to process input file data and validate inputs against the accepted patterns.

       It declares four properties statically and publicly which can be changed by user of this library for the purpose of

       customization.
       - maxAcceptablePrice: maximum value acceptable for price
       - maxAcceptableWeight: maximum value acceptable for weight
       - validateLineRegExp: RegExp pattern for validating single line of input file in a very basic format
       - validatePackageDataRegExp: RegExp pattern for validating package items within each line of input file.

       The implementation is done based on SOLID principle

       Note: The application ignores the first line of input data file. Leave it for description purpose
 */

public class Packer {

    public static  Integer maxAcceptablePrice = 100;
    public static  Integer maxAcceptableWeight = 100;
    public static  String validateLineRegExp="^\\d{1,2}\\s?(:).*$";
    public static  String validatePackageDataRegExp="^(\\()\\d{1,2}(,)\\d{1,2}\\.\\d{2}(,)(€)\\d{1,2}(\\))$";

    public static String pack(String dataFile) throws ApiException {
        Path path = Paths.get(dataFile);
        if (!Files.exists(path))
            throw new ApiException("Data file not found in given path!");

        try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {

            // using Java 8 parallel streaming for fast data extraction and processing
            return reader.lines().parallel()
                    .filter(row -> row.matches(validateLineRegExp))
                    .map(row -> packRow(row))
                    .collect(Collectors.joining("\n", "", "\n"));

        } catch (IOException ex) {
            throw new ApiException("Error reading data file. " + ex.getMessage());
        }
    }

    /*
        This method only process valid line of data and skips any invalid inputs
     */
    public static String packRow(String row) {
        if (!row.matches(validateLineRegExp))
            return "";

        Double weightLimit = Double.valueOf(row.substring(0, row.indexOf(":")));
        if (weightLimit > maxAcceptableWeight)
            return "";

        try {
            return pack(DataExtractor.extractItemPackage(row));
        }catch (ApiException e) {
            // we skip any invalid input
            return "";
        }
    }

    /*
        This is the core algorithm implemented for Packing particular Package. After we found first eligible

        item within the package, we exclude that item from the Package items by setting it's 'picked' property to true

        and then update the Package weight limit to new value and repeat the iterations till we longer find any eligible

        item within the Package
     */

    private static String pack(Package aPackage) {
        StringBuilder builder = new StringBuilder();

        while (aPackage.hasNext()){
            Item item = aPackage.next();

            if (!builder.toString().isEmpty())
                builder.append(",");

            builder.append(item.getIndex());

            item.setPacked(true);

            aPackage.setWeightLimit(aPackage.getWeightLimit() - item.getWeight());
        }

        return builder.toString();
    }
}
