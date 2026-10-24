package kz.itent.prep;
import com.google.gson.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import kz.itent.prep.data.Models.*;
import kz.itent.prep.domain.ScoreCalculator;

public class ContentValidationTest {
 private final Path root=Paths.get(".."); private final Gson gson=new Gson();
 @Test public void coreContentAndVariantsAreValid() throws Exception { validate(root.resolve("app/src/main/assets/subjects/mathematics")); validate(root.resolve("app/src/main/assets/subjects/physics")); validate(root.resolve("update/informatics/src/main/assets/subjects/informatics")); }
 private String readFile(Path path)throws Exception{StringBuilder out=new StringBuilder();try(BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()),StandardCharsets.UTF_8))){String line;while((line=reader.readLine())!=null)out.append(line).append('\n');}return out.toString();}
 private void validate(Path dir)throws Exception{
  Subject s=gson.fromJson(readFile(dir.resolve("subject.json")),Subject.class);assertNotNull("subject id",s.id);
  QuestionBank bank=gson.fromJson(readFile(dir.resolve(s.questionsFile)),QuestionBank.class);assertEquals(s.id,bank.subjectId);assertTrue("question count for "+s.id,bank.questions.size()>=250);
  Set<String> ids=new HashSet<>(), texts=new HashSet<>();Map<String,Question> byId=new HashMap<>();Map<String,Integer> topics=new HashMap<>(),difficulty=new HashMap<>();
  for(Question q:bank.questions){String at=s.id+" / "+q.id;assertTrue(at+" duplicate id",ids.add(q.id));assertTrue(at+" duplicate text",texts.add(q.text));assertNotNull(at+" text",q.text);assertFalse(at+" empty text",q.text.trim().isEmpty());assertEquals(at+" choices",4,q.options.size());assertEquals(at+" repeated choices",4,new HashSet<>(q.options).size());assertTrue(at+" correct index",q.correctIndex>=0&&q.correctIndex<4);assertFalse(at+" explanation",q.explanation.trim().isEmpty());assertTrue(at+" difficulty",q.difficulty>=1&&q.difficulty<=3);assertTrue(at+" source",Arrays.asList("original","adapted","public").contains(q.source));byId.put(q.id,q);topics.merge(q.topic,1,Integer::sum);difficulty.merge(String.valueOf(q.difficulty),1,Integer::sum);}
  assertTrue("topic variety: "+s.id,topics.size()>=5);assertTrue("difficulty variety",difficulty.size()==3);
  VariantBank vb=gson.fromJson(readFile(dir.resolve(s.variantsFile)),VariantBank.class);assertTrue("variant count",vb.variants.size()>=5);int expected=1;for(Variant v:vb.variants){assertEquals("variant numbering",expected++,v.number);assertEquals("variant length "+v.number,s.questionsPerTest,v.questionIds.size());assertEquals("variant duplicate ids",v.questionIds.size(),new HashSet<>(v.questionIds).size());Set<String> variantTopics=new HashSet<>();for(String id:v.questionIds){assertTrue("missing "+id,byId.containsKey(id));variantTopics.add(byId.get(id).topic);}assertTrue("variant topic coverage "+v.number,variantTopics.size()>=5);}
 }
 @Test public void scoringAndDeadlineBoundariesWork(){List<Question> qs=new ArrayList<>();Question a=new Question();a.correctIndex=1;Question b=new Question();b.correctIndex=0;qs.add(a);qs.add(b);assertEquals(1,ScoreCalculator.score(qs,new int[]{1,-1}));assertEquals(0,ScoreCalculator.remainingMillis(100,101));assertEquals(10,ScoreCalculator.remainingMillis(110,100));}
}
