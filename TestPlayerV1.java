package u7a4;

import java.util.ArrayList;
import java.util.Random;

import reversi.Arena;
import reversi.Coordinates;
import reversi.GameBoard;
import reversi.ReversiPlayer;
import reversi.Utils;
import u7a4.MinMaxPlayer.TimeOutException;

public class TestPlayerV1 implements ReversiPlayer
{
	/**
	 * Die Farbe des Spielers.
	 */
	private int						color			= 0;
	private long					timeLimit		= 0;
	private long					timeout			= 0;
	private static int				MAXDEPTH		= 30;
	private int test = 0;

	/**
	 * Alle mˆglichen Z¸ge werden in diesem Array gespeichert.
	 */
	private ArrayList<Coordinates>	possibleMoves	= new ArrayList<Coordinates>();

	/**
	 * Konstruktor, der bei der Gr√ºndung eines RandomPlayer eine Meldung auf
	 * den Bildschirm ausgibt.
	 */
	public TestPlayerV1()
	{
		System.out.println("TestPlayer erstellt.");
	}

	/**
	 * Speichert die Farbe und den Timeout-Wert in Instanzvariablen ab. Diese
	 * Methode wird vor Beginn des Spiels von {@link Arena} aufgerufen.
	 * 
	 * @see reversi.ReversiPlayer
	 */
	public void initialize(int color, long timeout)
	{
		this.color = color;
		if (color == GameBoard.RED)
		{
			System.out.println("GreedyPlayer ist Spieler RED.");
		} else if (color == GameBoard.GREEN)
		{
			System.out.println("GreedyPlayer ist Spieler GREEN.");
		}
		this.timeLimit = timeout;
		fillLookUpTable();
		createAllPossibleMoves();
	}

	public Coordinates nextMove(GameBoard gb)
	{
		timeout = System.currentTimeMillis() + timeLimit - 10;
		Move next = new Move(null, Integer.MIN_VALUE);
		try
		{
			for (int i = 0; i < MAXDEPTH; i++)
			{
				System.out.println("V1 Depth:" + i);
				next = max(gb, Integer.MIN_VALUE, Integer.MAX_VALUE, i, null);
			}
		} catch (TimeOutException e)
		{
		}
		return next.coord;
	}

	private Move max(GameBoard gb, int alpha, int beta, int depth, Coordinates last) throws TimeOutException
	{
		if (System.currentTimeMillis() >= timeout)
			throw new TimeOutException();
		if (depth == 0)
			return new Move(null, eval(gb, color, last));

		ArrayList<Coordinates> currentPossibleMoves = getCurrentPossibleMoves(gb.clone(), color);

		if (currentPossibleMoves.isEmpty())
		{
			if (gb.isMoveAvailable(Utils.other(color)))
			{
				return new Move(null, (min(gb, alpha, beta, depth - 1, null)).value);
			} else
				return new Move(null, eval(gb, color, null));
		}
		Move best = new Move(null, Integer.MIN_VALUE);
		for (Coordinates coord : currentPossibleMoves)
		{
			GameBoard newGB = gb.clone();
			newGB.checkMove(color, coord);
			newGB.makeMove(color, coord);

			Move result = min(newGB, alpha, beta, depth - 1, coord);
			alpha = Math.max(alpha, result.value);

			if (result.value > best.value)
			{
				best.coord = coord;
				best.value = result.value;
			}
			if (alpha >= beta)
				break;
		}
		return new Move(best.coord, alpha);
	}

	private Move min(GameBoard gb, int alpha, int beta, int depth, Coordinates last) throws TimeOutException
	{
		if (System.currentTimeMillis() >= timeout)
			throw new TimeOutException();
		if (depth == 0)
			return new Move(null, eval(gb, color, null));

		ArrayList<Coordinates> currentPossibleMoves = getCurrentPossibleMoves(gb.clone(), Utils.other(color));
		if (currentPossibleMoves.isEmpty())
		{
			if (gb.isMoveAvailable(color))
			{
				return new Move(null, (max(gb, alpha, beta, depth - 1, null)).value);
			} else
				return new Move(null, eval(gb, color, null));
		}
		Move best = new Move(null, Integer.MAX_VALUE);
		for (Coordinates coord : currentPossibleMoves)
		{
			GameBoard newGB = gb.clone();
			newGB.checkMove(Utils.other(color), coord);
			newGB.makeMove(Utils.other(color), coord);
			Move result = max(newGB, alpha, beta, depth - 1, coord);
			beta = Math.min(beta, result.value);

			if (result.value < best.value)
			{
				best.coord = coord;
				best.value = result.value;
			}

			if (beta <= alpha)
				break;
		}
		return new Move(best.coord, beta);
	}

	int eval(GameBoard gb, int playerColor, Coordinates move)
	{
		return gb.countStones(color);
	}

	private ArrayList<Coordinates> getCurrentPossibleMoves(GameBoard gb, int pColor)
	{
		ArrayList<Coordinates> currentPossibleMoves = new ArrayList<Coordinates>();
		for (int i = 0; i < possibleMoves.size(); i++)
		{
			if (gb.checkMove(pColor, possibleMoves.get(i)))
			{
				currentPossibleMoves.add(possibleMoves.get(i));
			}
		}
		return currentPossibleMoves;

	}

	private void createAllPossibleMoves()
	{
		for (int i = 1; i < 9; i++)
		{
			for (int j = 1; j < 9; j++)
			{
				possibleMoves.add(new Coordinates(i, j));
			}
		}
	}

	class Move
	{
		public Coordinates	coord;
		public int			value;

		public Move(Coordinates coord, int value)
		{
			this.coord = coord;
			this.value = value;
		}
	}

	class TimeOutException extends Exception
	{
		private static final long serialVersionUID = 1L;
	}
}
