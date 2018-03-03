copy from https://github.com/gradle/kotlin-dsl/tree/master/samples/multi-kotlin-project-with-buildSrc  
# mass-power  
after clone cd submodule-server and $git checkout master  
  
./gradlew run    
  
чтобы узанть версию kotlin в каждом модуле выолнить ./gradlew dependencies  
  
##Desktop
./gradlew desktop:build    
java -jar desktop/build/libs/mass-power.io-1.0.jar  
  
##Html  
./gradlew clean :submodule-html:compileKotlin2Js --refresh-dependencies --offline  
  
  
  