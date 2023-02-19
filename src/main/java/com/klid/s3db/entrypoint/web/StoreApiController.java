package com.klid.s3db.entrypoint.web;

import com.klid.s3db.model.dto.Sale;
import com.klid.s3db.model.dto.Store;
import com.klid.s3db.service.SaleManager;
import com.klid.s3db.service.StoreManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ivan Kaptue
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class StoreApiController {

  private final StoreManager storeManager;
  private final SaleManager saleManager;

  @GetMapping("/stores")
  public ResponseEntity<Page<Store>> getAll(
    @RequestParam(name = "page", required = false, defaultValue = "1") int page,
    @RequestParam(name = "size", required = false, defaultValue = "30") int size
  ) {
    log.info("Request start: getAll stores. page {}, size {}", page, size);
    var result = storeManager.getAll(page, size);
    log.info("Request end: getAll stores. page {}, size {}, result size: {}", page, size, result.getContent().size());
    return ResponseEntity.ok(result);
  }


  @GetMapping("/stores/{id}/sales")
  public ResponseEntity<Page<Sale>> getAllSales(
    @RequestParam(name = "page", required = false, defaultValue = "1") int page,
    @RequestParam(name = "size", required = false, defaultValue = "30") int size,
    @PathVariable(name = "id") String id
  ) {
    log.info("Request start: getAllSales. id {}, page {}, size {}", id, page, size);
    var result = saleManager.getAll(page, size, id);
    log.info("Request end: getAllSales. id {}, page {}, size {}, result size: {}", id, page, size, result.getContent().size());
    return ResponseEntity.ok(result);
  }
}
