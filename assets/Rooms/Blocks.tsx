<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.4" tiledversion="1.4.2" name="Blocks" tilewidth="32" tileheight="32" tilecount="7" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <properties>
   <property name="Type" value="Entrance_South"/>
  </properties>
  <image width="32" height="32" source="../Raw/Blocks1.png"/>
 </tile>
 <tile id="1">
  <properties>
   <property name="Type" value="Entrance_North"/>
  </properties>
  <image width="32" height="32" source="../Raw/Blocks2.png"/>
 </tile>
 <tile id="2">
  <properties>
   <property name="Type" value="Entrance_East"/>
  </properties>
  <image width="32" height="32" source="../Raw/Blocks3.png"/>
 </tile>
 <tile id="3">
  <properties>
   <property name="Type" value="Entrance_West"/>
  </properties>
  <image width="32" height="32" source="../Raw/Blocks4.png"/>
 </tile>
 <tile id="4">
  <properties>
   <property name="Type" value="Spawn"/>
  </properties>
  <image width="32" height="32" source="../Raw/Blocks5.png"/>
 </tile>
 <tile id="5">
  <properties>
   <property name="Type" value="Block"/>
  </properties>
  <image width="32" height="32" source="../Raw/Blocks.png"/>
 </tile>
 <tile id="6">
  <properties>
   <property name="MoveVertical" type="bool" value="true"/>
   <property name="Progress" type="float" value="0"/>
  </properties>
  <image width="32" height="32" source="../Raw/Bomb.png"/>
 </tile>
</tileset>
