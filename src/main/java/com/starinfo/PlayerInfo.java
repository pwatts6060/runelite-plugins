package com.starinfo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerInfo
{
	public String username;
	public int level;
	public double pickTicks;
	public boolean ring;
}
