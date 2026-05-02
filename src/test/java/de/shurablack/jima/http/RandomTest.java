package de.shurablack.jima.http;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import de.shurablack.jima.model.item.ItemInspection;

public class RandomTest {

    @Test
    public void test_how_many_requests() {
        Set<String> ids = IntStream.range(0, 50)
            .mapToObj(i -> "str_" + i)
            .collect(Collectors.toSet());


        List<Response<ItemInspection>> results = Requester.getMultipleItemInspections(ids);

        ResponseList<ItemInspection> list = new ResponseList<>(results);
        List<ItemInspection> items = list.getSuccessful();
        assertTrue(items.size() == 50);
    }
}
