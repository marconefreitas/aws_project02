package br.com.marcone.aws_project02.controller;

import br.com.marcone.aws_project02.model.ProductEventLogDto;
import br.com.marcone.aws_project02.repository.ProductEventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class ProductEventLogController {

    private ProductEventLogRepository productEventLogRepository;

    @Autowired
    public ProductEventLogController(ProductEventLogRepository repository) {
        this.productEventLogRepository = repository;
    }

    @GetMapping("/events")
    public List<ProductEventLogDto> getAllEvents(){

        return StreamSupport.stream(productEventLogRepository.findAll().spliterator(), false)
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}")
    public List<ProductEventLogDto> findAllByCode(@PathVariable String code){

        return productEventLogRepository.findAllByPk(code)
                .stream()
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}/{event}")
    public List<ProductEventLogDto> findAllByCodeAndEventType(@PathVariable String code,
                                                              @PathVariable String event){

        return productEventLogRepository.findAllByPkAndSkStartsWith(code, event)
                .stream()
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }
}
