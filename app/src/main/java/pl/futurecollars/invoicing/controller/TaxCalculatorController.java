package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.dto.TaxCalculation;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.service.TaxCalculatorService;

@Api(tags = {"tax-calculator-controller"})
@Slf4j
@RestController
@RequestMapping("/api/tax")
@AllArgsConstructor
public class TaxCalculatorController {

    private final TaxCalculatorService taxCalculatorService;

    @ApiOperation(value = "Get tax calculation")
    @PostMapping
    public ResponseEntity<TaxCalculation> getTaxCalculation(@RequestBody Company company) {
        log.debug("Getting tax calculation");
        return ResponseEntity.ok().body(taxCalculatorService.getTaxCalculation(company));
    }
}
