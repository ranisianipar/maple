package com.maple;

import com.maple.Exception.MapleException;
import com.maple.MockingObject.FakeObjectFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@ComponentScan(basePackageClasses = {ItemService.class})
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CounterServiceTest counterService;

    @Mock
    private AssignmentService assignmentService;

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createdDate"));

    Item item = FakeObjectFactory.getFakeItemBasic();

    Page<Item> itemPageMock = Mockito.mock(Page.class);

    @Test
    public void getAllItemWithoutSearch() {
        when(itemRepository.findAll(pageable)).thenReturn(itemPageMock);
        assertEquals(itemPageMock.getContent(), itemService.getAll(null, pageable));
    }

    @Test
    public void getAllItemWithSearch() {
        when(itemRepository.findByNameLike("haha",pageable)).thenReturn(itemPageMock);
        assertEquals(itemPageMock.getContent(), itemService.getAll("haha", pageable));
    }

    @Test
    public void getItemTestNotFound() {
        boolean thrown = false;
        try {
            itemService.get("ITM-1");
        }   catch (MapleException m){
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void getItemTestSucceed() throws MapleException{
        when(itemRepository.findById("ITM-0")).thenReturn(Optional.of(item));
        assertEquals(item, itemService.get("ITM-0"));
    }

    @Test
    public void getTotalObject() {
        when(itemRepository.count()).thenReturn((long) 0);
        assertEquals(0, itemService.getTotalObject());
    }

    @Test
    public void getTotalPages() {
        when(itemRepository.count()).thenReturn((long) 100);
        assertEquals(10, itemService.getTotalPage(10));
        assertEquals(13, itemService.getTotalPage(8));
    }

    @Test
    public void updateManyItemQuantity() {
        List items = new ArrayList();
        items.add(item);
        when(itemRepository.saveAll(items)).thenReturn(items);
        // VOID
        itemService.updateManyItemQuantity(items);
    }

    // CREATE

    // UPDATE

    // DeleteMany

    // Generate PdF
    @Test
    public void generatePdf() throws Exception{
        when(itemRepository.findById("ITM-0")).thenReturn(Optional.of(item));
        assertNotEquals(0, itemService.generatePdf("ITM-0").length);
    }

    @Test
    public void itemExistingChecker() {
        when(itemRepository.findById("ITM-0")).thenReturn(Optional.of(item));
        assertTrue(itemService.isExist("ITM-0"));
    }

    @Test
    public void returnItemSucceed() {
        when(itemRepository.findById("ITM-0")).thenReturn(Optional.of(item));
        // VOID
        itemService.returnItem("ITM-0",50);
    }




}
