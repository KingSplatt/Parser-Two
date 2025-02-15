## Explicación del proyecto

Se implementara un parser recursivo - Descendente basado en el pseudocódigo visto en clase, con su respectivo scanner(lexer) para la gramatica aqui descrita

Si el parser o el scanner encuentra errores debera desplegar el error y despues parar la compilación (Tambien puede continuar buscando mas errores)

El proyecto se probará con dos programas con errores y uno sin errores.

### Gramatica

```bash
  P → D S
  D → id (int | string | dou ) ε ; D
  D → ε
  S → IF E { S }
  S → IF E { S } ELSE { S }
  S → ID = OPER
  S → print OPER
  S → read OPER
  E → ID | D == ID | ID !== ID | ID > ID | ID >= ID | ID < ID | ID <= ID
  OPER → SUMA | RESTA | MULM | DIVM | ID
  SUMA → NUM + NUM | NUM + FRACC | FRACC + NUM | FRACC + FRACC
  RESTA → NUM – NUM | NUM – FRACC | FRACC – NUM | FRACC – FRACC
  MULM → NUM * NUM | NUM * FRACC | FRACC * NUM | FRACC * FRACC
  DIVD → NUM / NUM | NUM / FRACC | FRACC / NUM | FRACC / FRACC
  ID → LETRA (LETRA|DIGITO)*
  NUM → DIGITO (DIGITO)*
  DIGITO → 0 | 1 | 2 | 3 | 5 | 6 | 7 | 8 | 9
  FRACC → DIGITO(DIGITO)*.DIGITO(DIGITO)*
  CADENA → LETRA(LETRA)*
  LETRA → [A-Za-z]
```

### primer código (error)

```bash
  x1 int ; print t4 +
  Error (se esperaba un id)
```

### segundo código (error)

```bash
  apuntes83 int ; persona string ; while persona do print print
  Error (se esperaba una expresion o un id)
```

### Tercer código (sin error)

```bash
  a83 int ; b32 string ; IF a83 == b32 {
  a83 = 15 + 2 ;
  } ELSE {
  a83 = 15 - 2 ;
  }
```
