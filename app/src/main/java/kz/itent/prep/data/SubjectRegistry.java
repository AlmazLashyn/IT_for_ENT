package kz.itent.prep.data;
import android.content.Context;
import android.content.res.AssetManager;
import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import kz.itent.prep.data.Models.*;

public class SubjectRegistry {
 private final AssetManager assets; private final ExecutorService executor=Executors.newSingleThreadExecutor(); private final Gson gson=new Gson(); private volatile List<Subject> cache;
 public SubjectRegistry(Context context){assets=context.getAssets();}
 public void load(Callback<List<Subject>> callback){executor.execute(()->{try{if(cache==null){List<Subject> found=new ArrayList<>(); for(String folder:assets.list("subjects")){try{Subject s=gson.fromJson(read("subjects/"+folder+"/subject.json"),Subject.class); if(s!=null&&s.id!=null)found.add(s);}catch(Exception ignored){}} found.sort(Comparator.comparing((Subject s)->!"core".equals(s.type)).thenComparingInt(s->s.order));cache=found;} callback.done(cache,null);}catch(Exception e){callback.done(Collections.emptyList(),e);}});}
 public void loadQuestions(Subject s,Callback<QuestionBank> cb){executor.execute(()->{try{cb.done(gson.fromJson(read("subjects/"+s.id+"/"+s.questionsFile),QuestionBank.class),null);}catch(Exception e){cb.done(null,e);}});}
 public void loadVariants(Subject s,Callback<VariantBank> cb){executor.execute(()->{try{cb.done(gson.fromJson(read("subjects/"+s.id+"/"+s.variantsFile),VariantBank.class),null);}catch(Exception e){cb.done(null,e);}});}
 private String read(String path)throws IOException{try(InputStream in=assets.open(path);ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);return out.toString(StandardCharsets.UTF_8.name());}}
 public interface Callback<T>{void done(T value,Exception error);}
}
