package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatherServiceImpl implements GatherService{
    private final GatherRepository gatherRepository;

    // 모임생성
    public void createGather(GatherRequest request){
        Gather gather = new Gather(request.getTitle());
        gatherRepository.save(gather);
    }

    //모임 수정gather
    public void modifyGather(GatherRequest request, long id){
        Gather gather = gatherRepository.findById(id).orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.updateGatherTitle(request.getTitle());
        gatherRepository.save(gather);
    }

    //모임 삭제
    public void deleteGather(long id){
        Gather gather = gatherRepository.findById(id).orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.delete();
        gatherRepository.save(gather);
    }

    //모임 불러오기
    public List<Gather> Gathers(Pageable pageable) {

       return gatherRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
    }

}
