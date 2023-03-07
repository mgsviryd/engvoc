package by.sviryd.engvoc.service.registry;

import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.sun.imageio.plugins.gif.GIFImageWriterSpi;
import com.sun.imageio.plugins.png.PNGImageReaderSpi;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi;
import com.sun.imageio.plugins.wbmp.WBMPImageWriterSpi;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;

@Service
public class IIORegistryService {
    private IIORegistry registry;

    @PostConstruct
    public void init() {
        registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProviders(ServiceRegistry.lookupProviders(PNGImageReaderSpi.class));
        registry.registerServiceProviders(ServiceRegistry.lookupProviders(PNGImageWriterSpi.class));
        registry.registerServiceProviders(ServiceRegistry.lookupProviders(GIFImageReaderSpi.class));
        registry.registerServiceProviders(ServiceRegistry.lookupProviders(GIFImageWriterSpi.class));
        registry.registerServiceProviders(ServiceRegistry.lookupProviders(WBMPImageReaderSpi.class));
        registry.registerServiceProviders(ServiceRegistry.lookupProviders(WBMPImageWriterSpi.class));
    }
}
