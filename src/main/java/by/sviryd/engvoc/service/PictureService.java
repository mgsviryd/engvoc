package by.sviryd.engvoc.service;

import by.sviryd.engvoc.domain.Picture;
import by.sviryd.engvoc.repos.PictureRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureService {
    @Autowired
    private PictureRepo pictureRepo;
    public Picture save(Picture picture){
        return pictureRepo.save(picture);
    }
    public List<Picture> saveAll(List<Picture> pictures){
        return pictureRepo.saveAll(pictures);
    }
}
