import com.mobiquity.code.challenge.packer.Packer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackerTests {


    @Test
    @DisplayName("Test packing a data line with missing Package limit input value")
    public void testPackingDataWithMissingPackageLimitValue (){
        String dataRow = ": (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)";

        String expectedItems = Packer.packRow(dataRow);

        String actualItems = "";

        assertEquals(expectedItems, actualItems);
    }

    @Test
    @DisplayName("Test packing a data line with invalid input format, missing weight limit")
    public void testPackingDataWithInvalidInputFormat (){
        String dataRow = "(1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)";

        String expectedItems = Packer.packRow(dataRow);

        String actualItems = "";

        assertEquals(expectedItems, actualItems);
    }


    @Test
    @DisplayName("Test packing a data line with invalid input format, invalid pattern missing € symbol")
    public void testPackingDataWithInvalidInputFormatMissingEuroSymbol (){
        String dataRow = "81: (1,53.38,45) (2,88.62,98) (3,78.48,3) (4,72.30,76) (5,30.18,9) (6,46.34,48)";

        String expectedItems = Packer.packRow(dataRow);

        String actualItems = "";

        assertEquals(expectedItems, actualItems);
    }


    @Test
    @DisplayName("Test packing a data line with invalid input format, exceeding weight value")
    public void testPackingDataWithInvalidInputExceedingWeightValue (){
        String dataRow = "81: (1,153.38,€45) (2,188.62,€98) (3,178.48,€3) (4,172.30,€76) (5,130.18,€9) (6,146.34,€48)";

        String expectedItems = Packer.packRow(dataRow);

        String actualItems = "";

        assertEquals(expectedItems, actualItems);
    }


    @Test
    @DisplayName("Test packing a data line with invalid input format, exceeding price value")
    public void testPackingDataWithInvalidInputExceedingPriceValue (){
        String dataRow = "81: (1,53.38,€145) (2,88.62,€198) (3,78.48,€130) (4,72.30,€176) (5,30.18,€900) (6,46.34,€480)";

        String expectedItems = Packer.packRow(dataRow);

        String actualItems = "";

        assertEquals(expectedItems, actualItems);
    }


    @Test
    @DisplayName("Test packing a Package items with one candidate")
    public void testPackingOneCandidate (){

        String dataRow = "81: (1,53.38,€45) (2,88.62,€98) (3,78.48,€3) (4,72.30,€76) (5,30.18,€9) (6,46.34,€48)";

        String expectedItems = Packer.packRow(dataRow);

        String actualItems = "4";

        assertEquals(expectedItems, actualItems);
    }

    @Test
    @DisplayName("Test packing a Package with only one item ")
    public void testPackingWithOneItem (){

        String dataRow= "8 : (1,15.3,€34)";

        String actualItem= Packer.packRow(dataRow);

        String expectedItem = "";

        assertEquals(actualItem, expectedItem);
    }

    @Test
    @DisplayName("Test packing a Package items with two candidates")
    public void testPackingWithTwoCandidates() {
        String dataRow = "75 : (1,85.31,€29) (2,14.55,€74) (3,3.98,€16) (4,26.24,€55) (5,63.69,€52) (6,76.25,€75) (7,60.02,€74) (8,93.18,€35) (9,89.95,€78)";

        String actualItem= Packer.packRow(dataRow);

        String expectedItem = "2,7";

        assertEquals(actualItem, expectedItem);
    }

    @Test
    @DisplayName("Test packing a Package items with two candidates on equal weight situation")
    public void testPackingWithEqualsWeightItems(){

        String dataRow = "56 : (1,90.72,€13) (2,33.80,€40) (3,43.15,€10) (4,37.97,€16) (5,46.81,€36) (6,48.77,€79) (7,81.80,€45) (8,19.36,€79) (9,6.76,€64)";

        String actualItem= Packer.packRow(dataRow);

        String expectedItem = "8,9";

        assertEquals(actualItem, expectedItem);
    }

    @Test
    @DisplayName("Test packing complete data file")
    public void testCompleteDataFile (){
        String dataFile ="d:/trainings/data.txt";

        String actualItem= Packer.pack(dataFile);

        String expectedItem = "4" + "\n"+
                              "" + "\n"+
                              "2,7" + "\n"+
                              "8,9" + "\n";

        assertEquals(actualItem, expectedItem);
    }
}
