import com.example.bookshop.dao.OrderDao;
import com.example.bookshop.dao.OrderEntryDao;
import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderEntryDao orderEntryDao;

    private OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new OrderServiceImpl(orderDao, orderEntryDao);
    }

    @Test
    void createOrder_setsDefaultStatus() {
        Order order = new Order();
        order.setStatus(null);
        when(orderDao.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order saved = service.createOrder(order);

        assertSame(order, saved);
        assertEquals(OrderStatus.NEW, saved.getStatus());
        verify(orderDao).save(order);
    }

    @Test
    void updateOrderStatus_whenDaoReturnsZeroThrows() {
        when(orderDao.updateStatus(5L, OrderStatus.PAID)).thenReturn(0);
        assertThrows(RuntimeException.class, () -> service.updateOrderStatus(5L, OrderStatus.PAID));
    }

    @Test
    void deleteOrder_deletesEntriesAndOrder() {
        when(orderDao.deleteById(10L)).thenReturn(true);
        boolean result = service.deleteOrder(10L);
        assertTrue(result);
        verify(orderEntryDao).deleteByOrderId(10L);
        verify(orderDao).deleteById(10L);
    }
}
