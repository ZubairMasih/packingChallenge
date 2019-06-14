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

package com.mobiquity.code.challenge.models;

import com.mobiquity.code.challenge.exception.ApiException;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/*
    Package is consists of weight limit plus a collection of Items

    Finding the most eligible item for packing is done within pickup method
 */
public class Package implements Iterator<Item> {

    private double weightLimit;

    private List<Item> items;

    public Package(double weightLimit) {
        this.weightLimit = weightLimit;
        this.items = new ArrayList<>();
    }

    public Package(List<Item> items, double weightLimit) throws ApiException {

        this.weightLimit = weightLimit;

        this.items = items;
    }

    public double getWeightLimit() {
        return weightLimit;
    }

    public void setWeightLimit(double weightLimit){
        this.weightLimit = weightLimit;
    }

    @Override
    public boolean hasNext() {
        return processPacking().isPresent();
    }

    @Override
    public Item next() {
        return processPacking().get();
    }

    /*
       This method finds the eligible item for packing based on current weightLimit and higher price priority

       This is the core algorithm implemented for Packing particular Package. This algorithm first exclude items with higher

       weight than Package weight limit and then sort first based on higher price value and then within those items,

       it picks up the one with lower weight item in case we reach to two equals price items.

    */
    private Optional<Item> processPacking (){
        return this.items.stream()
                .filter(item -> item.getWeight() <= this.weightLimit && !item.isPacked())
                .sorted(Comparator.comparing(Item::getPrice, Comparator.reverseOrder()).
                        thenComparing(Item::getWeight))
                .findFirst();
    }
}
