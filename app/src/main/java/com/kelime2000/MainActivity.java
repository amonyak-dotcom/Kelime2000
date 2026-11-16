package com.kelime2000;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
 final Handler handler=new Handler(); final Random random=new Random();
 final ArrayList<Item> all=new ArrayList<>(), queue=new ArrayList<>();
 LinearLayout root; TextView heading,word,pron,timer,status; Button a,b,pass,speak;
 TextToSpeech tts; String category="Temel Kelimeler"; Item current; int correct=0,wrong=0,passed=0,expired=0,position=0,remaining=3; boolean locked=true; Runnable tick,advance; long generation=0;
 static class Item {String en,reading,tr,cat;Item(String e,String r,String t,String c){en=e;reading=r;tr=t;cat=c;}}
 @Override public void onCreate(Bundle b){super.onCreate(b);tts=new TextToSpeech(this,s->{if(s==TextToSpeech.SUCCESS)tts.setLanguage(Locale.US);});load();home();}
 TextView text(String s,int size){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(Color.rgb(25,35,50));v.setPadding(12,14,12,14);v.setGravity(Gravity.CENTER);return v;}
 Button button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setOnClickListener(v->r.run());return b;}
 void screen(String title){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(18,20,18,12);root.setBackgroundColor(Color.WHITE);ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);root.addView(text(title,25));}
 void load(){all.clear();try{BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("words.txt"),"UTF-8"));String s;while((s=br.readLine())!=null){String[] p=s.split("\\|",-1);if(p.length>=3)all.add(new Item(p[0],p[1],p[2],"Temel Kelimeler"));}br.close();}catch(Exception ignored){}
 try{JSONArray arr=new JSONArray(getPreferences(0).getString("custom","[]"));for(int i=0;i<arr.length();i++){JSONObject o=arr.getJSONObject(i);all.add(new Item(o.getString("en"),o.optString("reading"),o.getString("tr"),o.getString("cat")));}}catch(Exception ignored){}
 }
 void home(){invalidate();screen("Kelime 2000");root.addView(text("İki şıklı, 3 saniyelik İngilizce kelime testi",16));LinkedHashSet<String> cats=new LinkedHashSet<>();for(Item i:all)cats.add(i.cat);for(String c:cats){int count=0;for(Item i:all)if(i.cat.equals(c))count++;final String name=c;root.addView(button(c+" ("+count+" kelime)",()->start(name)));}root.addView(button("+ Kategori / Kelime İçe Aktar",this::importPage));root.addView(button("Soru Formatını Gör ve Kopyala",this::template));root.addView(text("Uygulama çevrimdışı çalışır. Seslendirme için telefonda İngilizce TTS sesi kurulu olmalıdır.",13));}
 void start(String cat){category=cat;queue.clear();for(Item i:all)if(i.cat.equals(cat))queue.add(i);Collections.shuffle(queue);position=correct=wrong=passed=expired=0;next();}
 void next(){invalidate();if(position>=queue.size()){results();return;}current=queue.get(position++);locked=false;remaining=3;long token=++generation;screen(category+" • "+position+"/"+queue.size());timer=text("⏱ 3 saniye",20);root.addView(timer);word=text(current.en,35);root.addView(word);pron=text("Okunuş: "+current.reading,17);root.addView(pron);speak=button("🔊 Telaffuzu Dinle",()->{if(tts!=null)tts.speak(current.en,TextToSpeech.QUEUE_FLUSH,null,"word");});root.addView(speak);
 String decoy=chooseDecoy(current);boolean first=random.nextBoolean();String left=first?current.tr:decoy,right=first?decoy:current.tr;
 a=button("A) "+left,()->answer(left.equals(current.tr),false,false));b=button("B) "+right,()->answer(right.equals(current.tr),false,false));pass=button("PASS / GEÇ",()->answer(false,true,false));root.addView(a);root.addView(b);root.addView(pass);status=text("",17);root.addView(status);
 tick=new Runnable(){public void run(){if(locked||token!=generation)return;remaining--;if(remaining<=0)answer(false,false,true);else{timer.setText("⏱ "+remaining+" saniye");handler.postDelayed(this,1000);}}};handler.postDelayed(tick,1000);
 }
 String chooseDecoy(Item i){ArrayList<Item> options=new ArrayList<>();for(Item x:all)if(x!=i&&!x.tr.equalsIgnoreCase(i.tr)&&x.cat.equals(i.cat))options.add(x);if(options.isEmpty())for(Item x:all)if(x!=i&&!x.tr.equalsIgnoreCase(i.tr))options.add(x);if(options.isEmpty())return "Bilinmiyor";
 // Prefer semantically related words through curated pairs; otherwise use a category peer.
 String[][] pairs={{"borrow","lend"},{"accept","refuse"},{"buy","sell"},{"before","after"},{"above","below"},{"early","late"},{"quiet","slow"},{"remember","forget"},{"open","close"},{"arrive","leave"},{"win","lose"},{"teach","learn"},{"give","take"},{"push","pull"},{"bring","carry"},{"ask","answer"},{"start","finish"},{"love","like"},{"hear","listen"},{"look","see"},{"speak","say"},{"large","huge"},{"small","little"},{"angry","sad"},{"happy","excited"}};
 for(String[] p:pairs){String target=i.en.equalsIgnoreCase(p[0])?p[1]:i.en.equalsIgnoreCase(p[1])?p[0]:null;if(target!=null)for(Item x:options)if(x.en.equalsIgnoreCase(target))return x.tr;}
 return options.get(random.nextInt(options.size())).tr;
 }
 void answer(boolean ok,boolean skip,boolean timeout){if(locked)return;locked=true;invalidate();a.setEnabled(false);b.setEnabled(false);pass.setEnabled(false);if(ok){correct++;next();return;}if(skip)passed++;else if(timeout)expired++;else wrong++;timer.setText("Cevap gösteriliyor (4 sn)");status.setText("Doğru cevap: "+current.tr+"\n"+current.en+" — "+current.reading);long token=++generation;advance=()->{if(token==generation)next();};handler.postDelayed(advance,4000);}
 void invalidate(){generation++;if(tick!=null)handler.removeCallbacks(tick);if(advance!=null)handler.removeCallbacks(advance);}
 void results(){screen("Test tamamlandı");root.addView(text("Doğru: "+correct+"\nYanlış: "+wrong+"\nPASS: "+passed+"\nSüre doldu: "+expired,22));root.addView(button("Tekrar Başla",()->start(category)));root.addView(button("Ana Menü",this::home));}
 final String SAMPLE="KATEGORI: Günlük Hayat\n\nborrow|borov|Ödünç almak\nlend|lend|Ödünç vermek\nquiet|kuayıt|Sessiz\nslow|slov|Yavaş";
 void template(){screen("Soru Kalıbı");root.addView(text("Her satır: İngilizce|Okunuş|Türkçe\nKATEGORI: başlığı yeni kategori oluşturur. Türkçe anlamı tekil ve net yazın.",16));TextView sample=text(SAMPLE,17);root.addView(sample);root.addView(button("📋 Formatı Kopyala",()->{((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Kelime formatı",SAMPLE));Toast.makeText(this,"Panoya kopyalandı",Toast.LENGTH_SHORT).show();}));root.addView(button("İçe Aktar",this::importPage));root.addView(button("Ana Menü",this::home));}
 void importPage(){screen("Kategori İçe Aktar");root.addView(text("Örnek formatı kopyalayıp kendi listenizi yapıştırın. Birden fazla KATEGORI: başlığı kullanılabilir.",16));EditText input=new EditText(this);input.setMinLines(9);input.setGravity(Gravity.TOP);input.setHint(SAMPLE);root.addView(input);root.addView(button("Kelime Listesini Kaydet",()->{String cat="Kendi Kelimelerim";int added=0;JSONArray saved;try{saved=new JSONArray(getPreferences(0).getString("custom","[]"));}catch(Exception e){saved=new JSONArray();}for(String line:input.getText().toString().split("\\r?\\n")){line=line.trim();if(line.isEmpty())continue;if(line.toUpperCase(Locale.ROOT).startsWith("KATEGORI:")){cat=line.substring(line.indexOf(':')+1).trim();if(cat.isEmpty())cat="Kendi Kelimelerim";continue;}String[] parts=line.split("\\|",-1);if(parts.length!=3||parts[0].trim().isEmpty()||parts[2].trim().isEmpty())continue;try{JSONObject obj=new JSONObject();obj.put("cat",cat);obj.put("en",parts[0].trim());obj.put("reading",parts[1].trim());obj.put("tr",parts[2].trim());saved.put(obj);added++;}catch(Exception ignored){}}getPreferences(0).edit().putString("custom",saved.toString()).apply();load();Toast.makeText(this,added+" kelime eklendi",Toast.LENGTH_LONG).show();home();}));root.addView(button("Formatı Göster",this::template));root.addView(button("Ana Menü",this::home));}
 @Override public void onBackPressed(){home();}
 @Override protected void onDestroy(){invalidate();if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
