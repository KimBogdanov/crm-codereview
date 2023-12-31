package ru.crm.system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.crm.system.database.entity.LogInfo;
import ru.crm.system.database.repository.LogInfoRepository;
import ru.crm.system.dto.loginfo.LogInfoCreateDto;
import ru.crm.system.mapper.LogInfoCreateMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogInfoService {

    private final LogInfoRepository logInfoRepository;
    private final LogInfoCreateMapper logInfoCreateMapper;

    @Transactional
    public Integer create(LogInfoCreateDto createDto) {
        return Optional.of(createDto)
                .map(logInfoCreateMapper::map)
                .map(logInfoRepository::save)
                .map(LogInfo::getId)
                .orElseThrow();
    }
}