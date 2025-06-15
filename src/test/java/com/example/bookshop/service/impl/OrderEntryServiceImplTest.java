import com.example.bookshop.dao.OrderEntryDao;
import com.example.bookshop.model.OrderEntry;
import com.example.bookshop.service.impl.OrderEntryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderEntryServiceImplTest {
    @Mock
    private OrderEntryDao orderEntryDao;
    private OrderEntryServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new OrderEntryServiceImpl(orderEntryDao);
    }

    @Test
    void createOrderEntries_delegatesToDao() {
        List<OrderEntry> entries = List.of(new OrderEntry());
        when(orderEntryDao.saveAll(entries)).thenReturn(entries);
        List<OrderEntry> result = service.createOrderEntries(entries);
        assertSame(entries, result);
        verify(orderEntryDao).saveAll(entries);
    }

    @Test
    void deleteOrderEntry_returnsDaoValue() {
        when(orderEntryDao.deleteById(8L)).thenReturn(true);
        assertTrue(service.deleteOrderEntry(8L));
        verify(orderEntryDao).deleteById(8L);
    }
}
