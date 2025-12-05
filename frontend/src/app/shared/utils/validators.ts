import { AbstractControl, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  static cnpjValido(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const cnpj = control.value;
      
      if (!cnpj) {
        return null; // Se não houver valor, não valida (required deve ser tratado separadamente)
      }

      // Remove caracteres não numéricos
      const cnpjLimpo = cnpj.replace(/[\D]/g, '');
      
      // Verifica se tem 14 dígitos
      if (cnpjLimpo.length !== 14) {
        return { cnpjInvalido: true };
      }

      // Elimina CNPJs inválidos conhecidos
      if (/^(\d)\1{13}$/.test(cnpjLimpo)) {
        return { cnpjInvalido: true };
      }

      // Valida DVs
      let tamanho = cnpjLimpo.length - 2;
      let numeros = cnpjLimpo.substring(0, tamanho);
      const digitos = cnpjLimpo.substring(tamanho);
      let soma = 0;
      let pos = tamanho - 7;
      
      for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2) {
          pos = 9;
        }
      }
      
      let resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11);
      if (resultado !== parseInt(digitos.charAt(0), 10)) {
        return { cnpjInvalido: true };
      }

      tamanho = tamanho + 1;
      numeros = cnpjLimpo.substring(0, tamanho);
      soma = 0;
      pos = tamanho - 7;
      
      for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2) {
          pos = 9;
        }
      }
      
      resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11);
      if (resultado !== parseInt(digitos.charAt(1), 10)) {
        return { cnpjInvalido: true };
      }

      return null; // CNPJ válido
    };
  }
}