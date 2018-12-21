package com.maple;

import com.maple.Exception.MapleException;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleUtils {

    @Autowired
    AssignmentService assignmentService;

    public static long getTotalObject(MongoRepository repo) {
        return repo.count();
    }

    public static long getTotalPages(long size, long totalObject) {
        if (totalObject % size==0)
            return totalObject/size;
        return (totalObject/size) + 1;
    }

    public static String storeFile(String folderPath, MultipartFile file, String id) throws IOException {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String path = folderPath+"/"+id+"_"+filename;
        if (!Files.exists(Paths.get(folderPath))) Files.createDirectory(Paths.get(folderPath));
        InputStream inputStream = file.getInputStream();
        Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        return path;
    }

    public static void deleteFile(String path) throws IOException{
        if (path == null) return;
        Files.deleteIfExists(Paths.get(path));
    }

    public static BaseResponse responseMapping(BaseResponse br, MapleException e){
        if (e == null) {
            br.setSuccess(true);
            br.setCode(HttpStatus.OK);
            return br;
        }
        br.setCode(HttpStatus.BAD_REQUEST);
        br.setErrorCode(e.getCode());
        br.setErrorMessage(e.getMessage());
        return br;
    }

    public static BaseResponse responseMappingAllEmployee(BaseResponse br, Iterator<Employee> employeeIterator) {
        List<EmployeeResponse> employeeResponses = new ArrayList<>();
        EmployeeResponse er;
        while (employeeIterator.hasNext()) {
            er = getEmployeeMap().map(employeeIterator.next(), EmployeeResponse.class);
            employeeResponses.add(er);
        }
        br.setValue(employeeResponses);
        return responseMapping(br, null);
    }

    public static MapperFacade getAssignmentMap() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Assignment.class, AssignmentResponse.class)
                .byDefault().exclude("itemSku").exclude("employeeId").register();
        return mapperFactory.getMapperFacade();
    }

    public static MapperFacade getEmployeeMap() {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(Employee.class, EmployeeResponse.class)
                .byDefault().exclude("password").register();
        return mapperFactory.getMapperFacade();
    }
}
