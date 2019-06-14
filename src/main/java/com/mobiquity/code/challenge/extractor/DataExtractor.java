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

package com.mobiquity.code.challenge.extractor;

import com.mobiquity.code.challenge.exception.ApiException;
import com.mobiquity.code.challenge.models.Item;
import com.mobiquity.code.challenge.models.Package;
import com.mobiquity.code.challenge.packer.Packer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
    This class is responsible for processing one line of data and compose a Package out of it

    This class skips all malicious data and extract only valid data based on this pattern:

    81: (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)
 */
public class DataExtractor {

    /*
        This method compose a Package object holding package weight with all valid related Items
     */
    public static Package extractItemPackage(String row) throws ApiException {

        if (!row.matches(Packer.validateLineRegExp))
            throw new ApiException("Invalid data row. please check your input to comply with given pattern");

        Double weightLimit = Double.valueOf(row.substring(0, row.indexOf(":")));
        if (weightLimit > Packer.maxAcceptableWeight)
            throw new ApiException("Invalid Package weight received!. The maximum acceptable package weight is "
                        + Packer.maxAcceptableWeight);

        String dataPart = row.substring(row.indexOf(":") + 1);
        if (dataPart.isEmpty()) {
            return new Package(weightLimit);
        }

        List<String> packageItems = List.of(dataPart.split(" "));

        List<Item> items =
                packageItems.stream().parallel()
                        .filter(part -> validPart(part))
                        .map(part -> composeItem(part))
                        .filter(item -> item.isPresent())
                        .map(optionalItem -> optionalItem.get())
                        .collect(Collectors.toList());

        return new Package(items, weightLimit);
    }

    /*
        This method extract an Item from valid input text

        The input format should comply with this pattern: (5,63.69,€52)

        The input value for price and weight must not exceed than predefined values currently set for 100

        Note: the input is already validated before is been passed to this method
     */
    private static Optional<Item> composeItem(String packageItem) {
        String parts[] = packageItem.split(",");

        int index = Integer.valueOf(parts[0].trim().substring(1));

        double weight = Double.valueOf(parts[1].trim());

        String pricePart = parts[2].trim().substring(1);

        int price = Integer.valueOf(pricePart.substring(0, pricePart.length()-1));
        if (price <= Packer.maxAcceptablePrice && weight <= Packer.maxAcceptableWeight)
            return Optional.of(new Item(index, weight, price));

        return Optional.empty();
    }

    /*
        Checks input against predefined Regular Expression based on this pattern: (5,63.69,€52)
        - Index value is an integer of maximum 2 digits
        - Weight value is a decimal with two digit precision and two digit number
        - Price value is an integer of maximum 2 digits preceding € sign.

        Note: space characters are not handled here. Therefore, input value should stick to above format
     */
    private static boolean validPart(String part) {
        return part.matches(Packer.validatePackageDataRegExp);
    }
}
