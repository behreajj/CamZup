import com.behreajj.camzup.core.*;
import com.behreajj.camzup.friendly.*;

Yup2 graphics;

Img img = Img.rgb(new Img(231, 123));

Rgb.AbstrToneMap toneMap = new Rgb.ToneMapHable();
Img.PpmFormat format = Img.PpmFormat.BINARY;
int levels = 256;
boolean usePremul = true;

void settings() {
    size(720, 405, Yup2.PATH_STR);
}

void setup() {
    frameRate(60.0f);
    graphics = (Yup2)getGraphics();

    byte[] arr = Img.toPpmBytes(
        img, new Rgb.ToneMapClamp(),
        format, levels, usePremul);
    saveBytes("/data/demo.ppm", arr);
    
    ImgExport.saveAs(
      sketchPath() + "\\data\\demo.png",
      img);
}
