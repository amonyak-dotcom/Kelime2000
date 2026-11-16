#!/usr/bin/env python3
"""Build a 2,000-item offline vocabulary asset from frequency-ranked bilingual open data."""
import json, urllib.request, pathlib, re, sys
BASE="https://raw.githubusercontent.com/recepyagmur/EnglishPractice/main/app/content/vocab/"
def fetch(name):
    request=urllib.request.Request(BASE+name,headers={"User-Agent":"Kelime2000-build/3"})
    with urllib.request.urlopen(request,timeout=45) as response:
        return json.load(response)
ranked=fetch("top3000.json")["words"]
lookup={}
for level in ("A1","A2","B1","B2","C1"):
    for item in fetch(level+".json")["words"]:
        key=item.get("w","").strip().lower()
        if key and item.get("tr","").strip() and key not in lookup:
            lookup[key]=item
result=[]; seen=set()
for item in sorted(ranked,key=lambda x:x["rank"]):
    en=item["w"].strip(); key=en.lower()
    info=lookup.get(key)
    if not info or key in seen: continue
    tr=info["tr"].strip().replace("|","/").replace("\n"," ")
    ipa=info.get("ipa","").strip().replace("|","/")
    if not tr or not re.search(r"[a-zA-Z]",en): continue
    result.append((en,ipa,tr));seen.add(key)
    if len(result)==2000:break
if len(result)!=2000:
    sys.exit(f"ERROR: Only {len(result)} ranked words have Turkish translations; refusing incomplete APK")
out=pathlib.Path("app/src/main/assets/words.txt");out.parent.mkdir(parents=True,exist_ok=True)
with out.open("w",encoding="utf-8") as f:
    for en,ipa,tr in result:f.write(f"{en}|{ipa}|{tr}\n")
print("Generated exactly",len(result),"unique frequency-ranked words, with Turkish and IPA when available")
print("First:",result[:3],"Last:",result[-3:])
