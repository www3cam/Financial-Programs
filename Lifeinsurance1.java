// Life insurance death simulator
// Actuarial mortality table located at http://www.ssa.gov/OACT/STATS/table4c6.html
//data also provided by the March 2013 10-q
//additional data provided by the Imperial holdings investor presentation: http://www.imperial.com/pdf/Imperial-Holdings-Investor-Presentation-June-6-2013.pdf
import java.util.*;


public class Lifeinsurance1
{
	public static double average = 0;//average npv
	public static double npv = 0, present = 0;//derived npv

	public static int cohort67 = 0, cohort72 = 77, cohort77whiteeagle = 213, cohort77 = 64, cohort82 = 182, cohort87 = 57, cohort92 = 8;//number of members in each cohort assumes the white eagle portfolio contains all the oldest and there for most NPV profitable people
	public static int death = 0, age67 = 67, age72 = 72, age87 = 87, age77we = 77, age77 = 77, age82 = 82, age92 = 92;//ages of all the cohorts I averaged all the buckets at the median
	public static int count = 0;

	public static double income = 0, whiteeagle = 0, expenses = 0;//amount of income before present value factor as well as white eagle portfolio value

	public static double premium = 0, premiumave = 0;
	public static void main(String[] args)
	{
		for (int i = 0; i < 1000; i++)
		{
			npv = 0;

			cohort67 = 3;
			cohort72 = 116;
			cohort77whiteeagle = 237;
			cohort77 = 59;
			cohort82 = 172; 
			cohort87 = 46; 
			cohort92 = 6;

			death = 0; 
			age67 = 67; 
			age72 = 72; 
			age87 = 87; 
			age77we = 77; 
			age77 = 77; 
			age82 = 82; 
			age92 = 92;

			count = 0;

			income = 0; 
			whiteeagle = 0; 
			expenses = 0;

			premium = 0;

			present = presentvalue();

			average = (average*i + present)/(i+1);

			premiumave = (premiumave*i+premium)/(i+1);


		}
		
		System.out.println(average);
		System.out.println(premiumave);
	}
	public static double presentvalue()
	{
		while(cohort67 + cohort72 + cohort77whiteeagle + cohort77 + cohort82 + cohort87 + cohort92 > 6)// loops around while the sum of the cohorts are greater than six.  It is uneconomic to hold 6 or less life insurance policies since fixed costs are around 25-30 million a year and the average payoff is 5 million per policy
		{
			income = 0;//reset income to be zero so when I add the death expenses doesn't add the previous income

			expenses = (cohort67 + cohort72 + cohort77whiteeagle + cohort77 + cohort82 + cohort87 + cohort92)*(40000000/639) + 35000000;//the expenses are premium expenses which is all the cohorts summed up times 40000000/639 + 2500000 for operating exepenses and 1000000 for interest expenses all expenses are conservative in my opinion

			double premiumadd = (cohort67 + cohort72 + cohort77whiteeagle + cohort77 + cohort82 + cohort87 + cohort92)*(40000000/639);

			premium = premium + premiumadd;
			//total yearly premium expense 40 million other expense is 25 million

			cohort67 = deathcalc(cohort67, age67);//sets the cohort amount to the remaining cohort number
			age67++;//cohort age goes up by one age++ means age = age + 1 
			income = income + 25000000/3.0*death;//death premium benifit
			
			cohort72 = deathcalc(cohort72, age72);//sets the cohort amount to the remaining cohort number
			age72++;//cohort age goes up by one
			income = income + 618022000/116.0*death;

			cohort77 = deathcalc(cohort77, age77);//sets the cohort amount to the remaining cohort number
			age77++;//cohort age goes up by one
			income = income + 1393584252/296.0*death;

			cohort77whiteeagle = deathcalc(cohort77whiteeagle, age77we);//sets the cohort amount to the remaining cohort number
			age77we++;//cohort age goes up by one
			whiteeagle = whiteeagle + 1393584252/296.0*death;
			if (whiteeagle < 76000000)//tests to see if the whiteeagle portfolio will pay out to Andrew Beal or fully pay to IFT
				income = income + 1393584252/296.0*death;
			else if (whiteeagle >= 76000000 && whiteeagle - 1393584252/296.0*death < 76000000)
				income = income + 76000000 - (whiteeagle - 1393584252/296.0*death) + .5 *(whiteeagle - 76000000);
			else if (whiteeagle >=76000000 && whiteeagle - 1393584252/296.0*death >= 76000000)
				income = income + .5 * 1393584252/296.0*death;

			cohort82 = deathcalc(cohort82, age82);//sets the cohort amount to the remaining cohort number
			age82++;//cohort age goes up by one
			whiteeagle = whiteeagle + 865539872/172.0*death;
			if (whiteeagle < 76000000)
				income = income + 865539872/172.0*death;
			else if (whiteeagle >= 76000000 && whiteeagle - 865539872/172.0*death < 76000000)
				income = income + 76000000 - (whiteeagle - 865539872/172.0*death) + .5 *(whiteeagle - 76000000);
			else if (whiteeagle >=76000000 && whiteeagle - 865539872/172.0*death >= 76000000)
				income = income + .5 * 865539872/172.0*death;

			cohort87 = deathcalc(cohort87, age87);//sets the cohort amount to the remaining cohort number
			age87++;//cohort age goes up by one
			whiteeagle = whiteeagle + 171672000/46.0*death;
			if (whiteeagle < 76000000)
				income = income + 171672000/46.0*death;
			else if (whiteeagle >= 76000000 && whiteeagle - 171672000/46.0*death < 76000000)
				income = income + 76000000 - (whiteeagle - 171672000/46.0*death) + .5 *(whiteeagle - 76000000);
			else if (whiteeagle >=76000000 && whiteeagle - 171672000/46.0*death >= 76000000)
				income = income + .5 * 171672000/46.0*death;

			cohort92 = deathcalc(cohort92, age92);//sets the cohort amount to the remaining cohort number
			age92++;//cohort age goes up by one
			whiteeagle = whiteeagle + 19242000/6.0*death;
			if (whiteeagle < 76000000)
				income = income + 19242000/6.0*death;
			else if (whiteeagle >= 76000000 && whiteeagle - 19242000/6.0*death < 76000000)
				income = income + 76000000 - (whiteeagle - 19242000/6.0*death) + .5 *(whiteeagle - 76000000);
			else if (whiteeagle >=76000000 && whiteeagle - 19242000/6.0*death >= 76000000)
				income = income + .5 * 19242000/6.0*death;


			count++;

			//System.out.println("count = " + count);
			//System.out.println("incom = " + income);
			//System.out.println("expenses = " + expenses);
			//System.out.println("discount rate = " + (Math.pow(1.15,count)));
			//System.out.println("cohort77whiteeagle = " + cohort77whiteeagle);


			npv = npv + (income - expenses)/(Math.pow(1.15,count));

			//System.out.println("NPV = " + npv);
			System.out.println(premiumadd + " " + count);

		}

		return npv;
	}

	public static int deathcalc(int cohort, int age)
	{
		//this method calculates the number a deaths each cohort has in a year
		if (cohort != 0)//skips this step if cohort has 0 in it
		{
			Random rand = new Random();

			int chort = 0;

			if (age == 67)
			{
				death = 0;;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();
					if (randnumb < ((.019138 + .012370)/2))//if randnumb is less than the probability of death then death gets incremented by one
						death++;//same as saying death = death + 1
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 68)
			{
				death = 0;;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies.  Equivalent to returning a binomial rv with parameters cohort number and probability of death ie the number enclosed in the first if statement.  
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.020752 + .013572)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 69)
			{
				death = 0;;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.022497 +.014908)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 70)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.024488+.016440)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 71)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.026747+.018162)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 72)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.029212+.0200019)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 73)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.031885 + .022003)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 74)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.034832 + .024173)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 75)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.038217+.026706)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 76)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.042059+ .029603)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 77)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.046261+.032718)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 78)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.050826+.036034)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 79)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.055865+.039683)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 80)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.061620 + .043899)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 81)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.068153+.048807)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 82)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.075349+.052374)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 83)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.083230+.060661)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 84)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.091933+.067751)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 85)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.101625+.075729)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 86)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.112448+.084673)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 87)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.124502+.094645)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 88)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.137837+.105694)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 89)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.152458+.117853)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 90)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.168352+.131146)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 91)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.185486+.145585)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 92)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.203817+.161175)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 93)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.223298+.177910)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 94)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.243867+.195774)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 95)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.264277+.213849)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 96)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.284168+.231865)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 97)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.303164+.249525)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 98)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.320876+.266525)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 99)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.336919 +.28250)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 100)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.353765+.299455)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 101)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.371454+.317422)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 102)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.390026+.3356467)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 103)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.409528+.356655)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 104)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.430004+.37805)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 105)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.451504+.400738)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 106)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.474079+.424738)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 107)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.497783+.450259)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 108)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.522673+.477285)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 109)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.548806+.505922)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 110)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.576246+.536978)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 111)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.605059+.568454)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 112)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.635312+.602561)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 113)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.667077+.638715)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 114)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.700431+.677038)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 115)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.735453+.717660)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 116)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.772225+.760720)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 117)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < ((.810837+.806363)/2))
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 118)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < .85137)
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else if (age == 119)
			{
				death = 0;
				for(int i = 0; i < cohort; i++)//for every number from 0 to the total number in the cohort generates a random number to see if person dies
				{
					double randnumb = rand.nextDouble();

					if (randnumb < .893947)
						death++;
					else
					{
						chort++;
					}
				}

				return chort;
			} else 
			{
				death = cohort;

				return 0;
			} 

		}
		return 0;
	}

}