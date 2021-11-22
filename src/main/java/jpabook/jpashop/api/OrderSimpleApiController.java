package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.*;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 *
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository; //의존관계 주입

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
       List<Order> all = orderRepository.findAllByString(new OrderSearch());

       for (Order order: all) {
           // getName을 하면 이 lazy가 강제 초기화 ->
           order.getMember().getName();
           order.getDelivery().getAddress();
       }
       return all;
    }

//    @GetMapping("/api/v2/simple-orders")
//    public List<SimpleOrderQueryDto> ordersV2() {
//        return orderRepository.findAllByString(new OrderSearch()).stream()
//                .map(SimpleOrderQueryDto::new)
//                .collect(toList());
//    }

//    @GetMapping("/api/v3/simple-orders")
//    public List<SimpleOrderQueryDto> ordersV3() {
//        return orderRepository.findAllWithMemberDelivery()
//                .stream().map(SimpleOrderQueryDto::new)
//                .collect(Collectors.toList());
//    }

    // V3 / V4는 둘 간의 우열을 가릴 수 없다.
    // 트레이드 오프가있음
    // V3는 외부를 건드리지 않고 엔터티 그대로 사용 -> 굉장이 많은 곳에서 사용가능. 변경해서 사용이 가능
    // V4는 쿼리를 sql짤 때 처럼 JPQL을 짜서 보냄 -> 재사용성이 떨어짐. 코드가 더러워짐. 대신 성능 최적화에서 좋다.
    // API 스펙에 맞춘 코드가 리파지토리에 그대로 또 들어가게 된다는 단점. -> 리파지토리는 엔터티 사용 용도로 사용되어야함.
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4() {
        return orderRepository.findOrderDtos();
    }

}
