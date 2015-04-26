package com.example.kevinhuang.spf420client;

import java.util.TimeZone;

/**
 * Created by Kevin Huang on 4/25/2015.
 */
interface GameSetting {
    public final TimeZone timeZone = TimeZone.getTimeZone("GMT+7");
    public final int TotalItem = 10;
    public final String[] ItemsName = new String[]{"Honey","Herbs","Clays","Minerals","Potion","Incense","Gems","Life Elixir","Mana Crystal","Philosopher Stone"};
    public final String[] ColomnName = new String[]{"No","OfferedItem","Sum","DesiredItem","Sum","Availabilty","Accept?"};
    public final int[] IdItemImg = new int[]{R.id.imghoney,R.id.imgherbs,R.id.imgclays,R.id.imgmineral,R.id.imgpotion,R.id.imgincense,R.id.imggems,R.id.imgelixir,R.id.imgcrystal,R.id.imgpsstone};
    public final int[] IdItemAmount = new int[]{R.id.txtamounthoney,R.id.txtamountherbs,R.id.txtamountclays,R.id.txtamountmineral,R.id.txtamountpotion,R.id.txtamountincense,R.id.txtamountgems,R.id.txtamountelixir,R.id.txtamountcrystal,R.id.txtamountpsstone};
    public final int[] ResItemImg = new int[]{R.drawable.honey,R.drawable.herbs,R.drawable.clay,R.drawable.mineral,R.drawable.potion,R.drawable.incense,R.drawable.gems,R.drawable.elixir,R.drawable.crystal,R.drawable.psstone};
    public final int[] ResBlankItemTag = new int[]{R.drawable.blankitem1,R.drawable.blankitem2,R.drawable.blankitem3};

}
