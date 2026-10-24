package kz.itent.prep.data;
import com.google.gson.annotations.SerializedName;
import java.util.*;

public final class Models {
 private Models() {}
 public static class Subject { public String id,title,type,icon,questionsFile,variantsFile,questionSetLabel; public int order,questionsPerTest,durationMinutes; }
 public static class QuestionBank { public int schemaVersion; public String subjectId; public List<Question> questions=new ArrayList<>(); }
 public static class Question { public String id,topic,text,code,explanation,source,sourceNote; public int difficulty,correctIndex; public List<String> options; }
 public static class VariantBank { public int schemaVersion; public String subjectId; public List<Variant> variants=new ArrayList<>(); }
 public static class Variant { public int number; public String title; public List<String> questionIds; }
 public static class Session { public String subjectId,mode; public List<Question> questions; public int[] answers; public int current; public long deadline; }
 public static class Result { public String subjectId,subjectTitle,mode; public long date,duration; public List<Question> questions; public int[] answers; public int score; }
}
