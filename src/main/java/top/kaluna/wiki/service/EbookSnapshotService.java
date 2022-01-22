package top.kaluna.wiki.service;

import org.springframework.stereotype.Service;
import top.kaluna.wiki.mapper.EbookSnapshotMapperCust;

import javax.annotation.Resource;

/**
 * @author Yuery
 * @date 2022/1/15/0015 - 23:36
 */
@Service
public class EbookSnapshotService {

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }
}
